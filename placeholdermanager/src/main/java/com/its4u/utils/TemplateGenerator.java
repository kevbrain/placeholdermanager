package com.its4u.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.its4u.models.templates.TemplateModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;

@Data
public class TemplateGenerator {

	private final Template argoAppTemplate;
	
	private final Template ocpNameSpaceTemplate;
	
	private final Template skopeoCopy;
	
	public TemplateGenerator() throws IOException {    	
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        
        cfg.setDirectoryForTemplateLoading(new File("//app//resources//templates/"));
        argoAppTemplate = cfg.getTemplate("argoAppTemplate.yaml");
        ocpNameSpaceTemplate = cfg.getTemplate("ocpNameSpaceTemplate.yaml");
        skopeoCopy = cfg.getTemplate("skopeo-copy.json");
      
    }
	
	public String generateOcpNameSpace(TemplateModel model) throws IOException, TemplateException {

        Writer out = new StringWriter();
        ocpNameSpaceTemplate.process(model, out);
        return out.toString();

    }
	
	public String generateArgoApp(TemplateModel model) throws IOException, TemplateException {

        Writer out = new StringWriter();
        argoAppTemplate.process(model, out);
        return out.toString();

    }
	
	public String generateSkopeoCopyEvent(TemplateModel model) throws IOException, TemplateException {

        Writer out = new StringWriter();
        skopeoCopy.process(model, out);
        return out.toString();

    }
}
