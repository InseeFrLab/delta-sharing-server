package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.InvalidTokenException;
import com.example.demo.service.SharedTableManager;

import io.onyxia.sharing.server.protocol.Protocol.ListSharesResponse;
import io.onyxia.sharing.server.protocol.Protocol.ListSharesResponse.Builder;
import io.onyxia.sharing.server.protocol.Protocol.Share;

@RestController
public class SharingController {


	private SharedTableManager sharedTableManager;

	public SharingController(SharedTableManager sharedTableManager) {
		super();
		this.sharedTableManager = sharedTableManager;
	}

	@GetMapping(path =  "/shares", produces = "application/json")
	public ListSharesResponse getShares(
			@RequestParam(value = "maxResults", defaultValue = "500") Integer maxResults,
			@RequestParam(value = "nextPageToken") @Nullable String nextPageToken) throws UnsupportedEncodingException, InvalidTokenException {

		Pair<List<Share>, String> data = sharedTableManager.listShares(nextPageToken, maxResults);
		//we could check how to handle null value in protobuf by configuration
		Builder b = ListSharesResponse.newBuilder();
		if(data.getValue0()!=null) {
			b.addAllItems(data.getValue0());
		}
		if(data.getValue1()!=null) {
			b.setNextPageToken(data.getValue1());
		}
		
		return b.build();
	}



}
