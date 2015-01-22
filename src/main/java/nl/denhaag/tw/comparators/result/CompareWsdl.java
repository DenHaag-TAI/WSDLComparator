package nl.denhaag.tw.comparators.result;

/*
 * #%L
 * wsdl-comparator
 * %%
 * Copyright (C) 2012 - 2013 Team Webservices (Gemeente Den Haag)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.denhaag.tw.comparators.wsdl.DescriptionComparator;

import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.w3c.dom.Document;

public class CompareWsdl {

	/**
	 * @param args
	 * @throws URISyntaxException
	 * @throws WSDLException
	 * @throws IOException
	 */
	public static CompareResult compare(File oldWsdl, File newWsdl) throws WSDLException, URISyntaxException,
			IOException {
		
		Description wsdlOne = readWsdl(oldWsdl);
		Description wsdlTwo = readWsdl(newWsdl);
		CompareResult compareResult = new CompareResult(Compatibility.NOCHANGE, "WSDL comparison");
		compareResult.addChild(new DescriptionComparator(wsdlOne, wsdlTwo).compare());
		ComparisonStorage.getInstance().clear();
		return compareResult;
	}

	private static Description readWsdl(File file) throws WSDLException, URISyntaxException, IOException {
		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		//FileInputStream inputStream = new FileInputStream();
		Description desc = reader.read(file.toURI().toURL());
		//inputStream.close();
		return desc;
	}
	public static void writeXmlFile(Document doc, String filename) {
	    try {
	        // Prepare the DOM document for writing
	        Source source = new DOMSource(doc);

	        // Prepare the output file
	        File file = new File(filename);
	        Result result = new StreamResult(file);

	        // Write the DOM document to the file
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	    } catch (TransformerConfigurationException e) {
	    } catch (TransformerException e) {
	    }
	}



}
