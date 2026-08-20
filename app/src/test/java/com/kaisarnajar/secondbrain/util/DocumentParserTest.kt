package com.kaisarnajar.secondbrain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentParserTest {

    @Test
    fun cleanTitleStripsExtensionAndFormatting() {
        assertEquals("Design Spec v1", DocumentParser.cleanTitle("Design_Spec_v1.md"))
        assertEquals("meeting notes", DocumentParser.cleanTitle("meeting-notes.txt"))
        assertEquals("Scanned Document", DocumentParser.cleanTitle("Scanned_Document.pdf"))
        assertEquals("photo", DocumentParser.cleanTitle("photo.png"))
        assertEquals("Untitled", DocumentParser.cleanTitle("Untitled"))
    }
}
