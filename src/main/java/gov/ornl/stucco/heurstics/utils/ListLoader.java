package gov.ornl.stucco.heurstics.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ListLoader {
	private static ObjectMapper mapper = new ObjectMapper();

	public static FreebaseList loadFreebaseList(String listFile, String listType) {
		FreebaseList freebaseList = null;
		try {
			InputStream inputStream = new FileInputStream(new File(listFile));
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			freebaseList = mapper.readValue(inputStream, FreebaseList.class);
			freebaseList.setListType(listType);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return freebaseList;
	}
	
	public static Set<String> loadTextList(String textFile) {
		Set<String> textList = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(textFile)));
			String term = reader.readLine();
			while (term != null) {
				textList.add(term);
				term = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return textList;
	}

	
	public static void main(String[] args) {
//		FreebaseList freebaseList = ListLoader.loadFreebaseList("src/main/resources/lists/software_info.json","software");
//		System.out.println(freebaseList.toString());
		Set<String> relTerms = ListLoader.loadTextList("src/main/resources/lists/relevant_terms.txt");
		System.out.println(relTerms);
	}

}
