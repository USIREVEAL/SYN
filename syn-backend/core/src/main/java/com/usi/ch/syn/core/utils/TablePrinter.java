package com.usi.ch.syn.core.utils;

import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class TablePrinter {

    static Logger logger = LoggerFactory.getLogger(TablePrinter.class);

    private TablePrinter() { }

    public static void printTable(Project project) {
        StringBuilder buffer1 = new StringBuilder();

        int lineLenght = 70 + (project.getProjectHistory().getProjectVersions().size() * 5) - 5;

        while (buffer1.length() < lineLenght) {
            buffer1.append("_");
        }
        String SEPARATOR = buffer1.toString();

        System.out.println(SEPARATOR);
        for (int i = 0; i < (lineLenght/ 2) - 10; i++) System.out.print(" ");
        System.out.println("Evolution History");
        System.out.println(SEPARATOR);

        project.getProjectHistory().getFileHistories().forEach(e -> {
            StringBuilder buffer = new StringBuilder();

            buffer.append(e.getName());
            buffer.append(":");

            while (buffer.length() < 70) {
                buffer.append(" ");
            }
            buffer.append("| ");
            Iterator<FileVersion> iterator = e.getFileVersions().iterator();
            FileVersion currentVersion = iterator.next();

            for (int i = 0; i <= project.getProjectHistory().getProjectVersions().size(); i++) {
                if (currentVersion.getId() == i) {
                    Change action = currentVersion.getChange();

                    if (action.isAdd()) {
                       buffer.append('A');
                    } else if (action.isDelete()) {
                        buffer.append('D');
                    }  else if (action.isRename()) {
                        buffer.append('R');
                    }  else if (action.isModify()) {
                        buffer.append('U');
                    } else if (action.isModify()) {
                        buffer.append('M');
                    }

                    if (iterator.hasNext()) currentVersion = iterator.next();
                } else {
                    buffer.append(" ");
                }
                buffer.append(" | ");
            }
            System.out.println(buffer);

        });

        System.out.println();
        System.out.println(SEPARATOR);
        for (int i = 0; i < (lineLenght/ 2) - 10; i++) System.out.print(" ");
        System.out.println("Version Indexes");
        System.out.println(SEPARATOR);
        project.getProjectHistory().getProjectVersions().forEach(v -> {
            System.out.println(v.getId() + ": " + v.getCommitHash());
        });
    }


}
