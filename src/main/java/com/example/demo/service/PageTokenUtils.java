package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import com.example.demo.exception.InvalidTokenException;
import com.google.protobuf.InvalidProtocolBufferException;

import io.onyxia.sharing.server.protocol.Protocol.PageToken;

public class PageTokenUtils {

	public static  Page getPage(String nextPageToken, String share, String schema, Integer maxResults,
			Integer totalSize) throws InvalidTokenException {
		Integer start = decodePageToken(nextPageToken, share, schema);
		if (start > totalSize) {
			throw new InvalidTokenException("invalid nextPageToken");
		}
		
		return new Page(start, maxResults, totalSize );
	}
	

	public static Integer decodePageToken(String pageTokenString, String expectedShare, String expectedSchema) throws InvalidTokenException {
		if(pageTokenString==null) {
			return 0;
		}
		PageToken pageToken = null;
		try {
			byte[] binary = Base64.getUrlDecoder().decode(pageTokenString.getBytes("utf8"));
			pageToken = PageToken.parseFrom(binary);
			if (StringUtils.isBlank(pageToken.getId())
					|| !StringUtils.equalsIgnoreCase(pageToken.getShare(), expectedShare)
					|| !StringUtils.equalsIgnoreCase(pageToken.getSchema(), expectedSchema)) {
				throw new InvalidTokenException("invalid nextPageToken");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidTokenException("invalid nextPageToken");
		}
		return Integer.decode(pageToken.getId());
	}
	
	public static String encodePageToken(String id, String share, String schema) throws UnsupportedEncodingException  {
		if(id!=null) {
			byte[] binary = PageToken.newBuilder().setId(id).build().toByteArray();
			return new String(Base64.getUrlEncoder().encode(binary), StandardCharsets.UTF_8);
		}
		else {
			return null;
		}
	}

}
