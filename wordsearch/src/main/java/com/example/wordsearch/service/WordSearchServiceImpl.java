package com.example.wordsearch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.wordsearch.dto.SearchTerm;

@Service("wordSearchService")
public class WordSearchServiceImpl implements WordSearchService {

	public static final Logger logger = LoggerFactory.getLogger(WordSearchServiceImpl.class);

	
	@Override
	public boolean isValid(SearchTerm searchterm) {

		if (null != searchterm && null != searchterm.getSearchKeyword()) {
			return searchterm.getSearchKeyword().size() > 0;
		}
		return false;
	}

	/** method to search against files
	 * @param searchWordList
	 * @param filePath
	 * @param matchList
	 * @throws IOException
	 */
	@Override
	public Map<String, List<String>> searchFiles(List<String> searchWordList, String dirPath) throws IOException {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<String> matchFileList = new ArrayList<String>();
		logger.info(" path " + dirPath);
		final Path path = Paths.get(dirPath);
		walkDirectory(searchWordList, path, matchFileList);
		logger.info(" matchFileList " + matchFileList);
		if(!matchFileList.isEmpty()) {
		result.put(searchWordList.toString(), matchFileList);
		}
		return result;
	}

	/** count word against files
	 * @param searchWordList
	 * @param filePath
	 * @param matchList
	 * @throws IOException
	 */
	private static void countWords(List<String> searchWordList, Path filePath, List<String> matchList)
			throws IOException {
		List<String> list = new ArrayList<String>();
		int matchCount = 0;
		boolean matchFound = false;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath.toUri()))) {
			list = new ArrayList<String>();
			list = br.lines().collect(Collectors.toList());
			for (String word : searchWordList) {
				matchFound = list.stream().anyMatch(str -> str.trim().startsWith(word));
				matchFound = list.stream().anyMatch(str -> str.trim().equals(word));
				if (!matchFound) {
					matchFound = list.stream().anyMatch(str -> str.trim().endsWith(word));
				}
				if (matchFound) {
					matchCount++;
				}
				if (matchCount == searchWordList.size()) {
					matchList.add(filePath.toUri().toString());
					continue;
				}

			}

		} catch (IOException e) {
			throw new IOException("Exception in Searching keywords" + e);
		}
		list = null;
	}

	
	/** walking files against directory
	 * @param searchWordList
	 * @param dirPath
	 * @param matchFileList
	 * @throws IOException
	 */
	private static void walkDirectory(List<String> searchWordList, Path dirPath, List<String> matchFileList)
			throws IOException {
		logger.info("coming here");
		if (Files.isDirectory(dirPath)) {
			// Iterate directory
			Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					countWords(searchWordList, file, matchFileList);
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			walkDirectory(searchWordList, dirPath, matchFileList);
		}

	}

}
