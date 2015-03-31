package com.atsebak.raspberrypi.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PIConsoleFilter implements Filter {
    private Project project;
    public PIConsoleFilter(@NotNull Project project) {
        this.project = project;
    }

    /**
     * Applys a text filter from the output
     *
     * @param line
     * @param entireLength
     * @return
     */
    @Nullable
    @Override
    public Result applyFilter(String line, int entireLength) {
        int afterLineNumberIndex = line.indexOf(": "); // end of file_name_and_line_number sequence
        if (afterLineNumberIndex == -1) {
            return null;
        }

        String fileAndLineNumber = line.substring(0, afterLineNumberIndex);
        int index = fileAndLineNumber.lastIndexOf(':');

        if (index == -1) {
            return null;
        }

        final String fileName = fileAndLineNumber.substring(0, index);
        String lineNumberStr = fileAndLineNumber.substring(index + 1, fileAndLineNumber.length()).trim();
        int lineNumber;
        try {
            lineNumber = Integer.parseInt(lineNumberStr);
        } catch (NumberFormatException e) {
            return null;
        }

        final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(fileName.replace(File.separatorChar, '/'));
        if (file == null) {
            return null;
        }

        int textStartOffset = entireLength - line.length();
        int highlightEndOffset = textStartOffset + afterLineNumberIndex;

        OpenFileHyperlinkInfo info = new OpenFileHyperlinkInfo(project, file, lineNumber - 1);
        return new Result(textStartOffset, highlightEndOffset, info);
    }

}
