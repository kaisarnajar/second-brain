package com.kaisarnajar.secondbrain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class DocumentParseResult(
    val title: String,
    val content: String
)

object DocumentParser {

    /**
     * Parses a local document or image Uri into a DocumentParseResult (title + extracted text content).
     */
    suspend fun parseDocument(context: Context, uri: Uri): DocumentParseResult = withContext(Dispatchers.IO) {
        val fileName = getFileName(context, uri) ?: "Imported Note"
        val title = cleanTitle(fileName)
        val mimeType = context.contentResolver.getType(uri)?.lowercase() ?: ""
        val extension = getExtension(fileName).lowercase()

        val content = when {
            mimeType.startsWith("image/") || extension in listOf("png", "jpg", "jpeg", "webp") -> {
                ImageParser.extractTextFromUri(context, uri)
            }
            mimeType == "application/pdf" || extension == "pdf" -> {
                extractTextFromPdf(context, uri)
            }
            else -> {
                // Default text / markdown parser (.txt, .md, etc.)
                extractTextFromStream(context, uri)
            }
        }

        DocumentParseResult(
            title = title.ifBlank { "Imported Document" },
            content = content.trim()
        )
    }

    /**
     * Reads text directly from a plain text or markdown stream buffer.
     */
    fun extractTextFromStream(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Extracts text from PDF by rendering each page with PdfRenderer and applying ML Kit OCR.
     */
    private suspend fun extractTextFromPdf(context: Context, uri: Uri): String {
        val pageTexts = mutableListOf<String>()
        var tempFile: File? = null
        var fileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null

        try {
            // Copy stream to temporary file for PdfRenderer descriptor access
            tempFile = File.createTempFile("pdf_import_", ".pdf", context.cacheDir)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)

            val pageCount = pdfRenderer.pageCount
            for (i in 0 until pageCount) {
                val page = pdfRenderer.openPage(i)
                val width = page.width * 2 // 2x scale for higher OCR clarity
                val height = page.height * 2
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val pageText = ImageParser.extractTextFromBitmap(bitmap)
                bitmap.recycle()

                if (pageText.isNotBlank()) {
                    pageTexts.add("--- Page ${i + 1} ---\n$pageText")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                pdfRenderer?.close()
                fileDescriptor?.close()
                tempFile?.delete()
            } catch (ignored: Exception) {}
        }

        return pageTexts.joinToString("\n\n")
    }

    /**
     * Derives the display file name from ContentResolver query or Uri path.
     */
    fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        name = it.getString(index)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                name = name?.substring(cut + 1)
            }
        }
        return name
    }

    /**
     * Strips extension and formats clean title from file name.
     */
    fun cleanTitle(fileName: String): String {
        val lastDot = fileName.lastIndexOf('.')
        val rawName = if (lastDot != -1) fileName.substring(0, lastDot) else fileName
        return rawName.replace('_', ' ').replace('-', ' ').trim()
    }

    private fun getExtension(fileName: String): String {
        val lastDot = fileName.lastIndexOf('.')
        return if (lastDot != -1) fileName.substring(lastDot + 1) else ""
    }
}
