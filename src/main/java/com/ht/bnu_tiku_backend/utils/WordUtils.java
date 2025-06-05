package com.ht.bnu_tiku_backend.utils;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

public class WordUtils {
    public static XWPFDocument getWord(List<String> list) {
        XWPFDocument doc = new XWPFDocument();
        for (String s : list) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            para.setAlignment(ParagraphAlignment.LEFT);
            run.setBold(true);
            run.setFontFamily("宋体");
            run.setFontSize(12);
            run.setText(s);
        }
        return doc;
    }
}