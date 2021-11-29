package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.example.demo.exception.InvalidTokenException;

import io.delta.sharing.server.config.ServerConfig;
import io.delta.sharing.server.config.ShareConfig;
import io.onyxia.sharing.server.protocol.Protocol.Schema;
import io.onyxia.sharing.server.protocol.Protocol.Share;

public class SharedTableManager {
	
	private ServerConfig serverConfig;

	public SharedTableManager(ServerConfig sc) {
		this.serverConfig=sc;
	}


	public Pair<List<Share>, String> listShares(String nextPageToken, Integer maxResults) throws InvalidTokenException, UnsupportedEncodingException{
		List<ShareConfig> shares = this.serverConfig.getShares();
		int totalSize = shares.size();
		Page page = PageTokenUtils.getPage(nextPageToken, null, null, maxResults, totalSize);
		List<Share> res = shares.subList(page.getStart(),page.getEnd()).stream().map(s -> Share.newBuilder().setName(s.getName()).build()).collect(Collectors.toList());
		Pair<List<Share>, String> result= new Pair<List<Share>, String>(res,
				PageTokenUtils.encodePageToken(page.getNextId(), null, null));
		return result;
	}

	public Pair<List<Schema>, String> listSchemas(String share, String pageToken, Integer maxResults) {
		return null;
	}
	


}
