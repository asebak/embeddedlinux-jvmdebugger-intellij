package com.atsebak.raspberrypi.utils;

import freemarker.template.Configuration;
import lombok.Builder;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

@Builder
public class Template {
    private Map<String, Object> data;
    private String outputFile;
    private Class<?> classContext;
    private String name;

    /**
     * Processes a freemarker template based on some parameters
     */
    public void toFile() {
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(classContext, "/");
        try {
            freemarker.template.Template template = configuration.getTemplate(name);
            Writer file = new FileWriter(new File(outputFile));
            template.process(data, file);
            file.flush();
            file.close();

        } catch (Exception e) {

        }
    }

}
