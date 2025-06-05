/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appsdeveloperblog.photoapp.api.albums.io.controllers;

import com.appsdeveloperblog.photoapp.api.albums.data.AlbumEntity;
import com.appsdeveloperblog.photoapp.api.albums.messaging.AlbumMessage;
import com.appsdeveloperblog.photoapp.api.albums.messaging.AlbumMessageProducer;
import com.appsdeveloperblog.photoapp.api.albums.messaging.CreateAlbumRequestModel;
import com.appsdeveloperblog.photoapp.api.albums.service.AlbumsService;
import com.appsdeveloperblog.photoapp.api.albums.ui.model.AlbumResponseModel;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.modelmapper.ModelMapper;
import java.lang.reflect.Type;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/albumsData")
public class AlbumsController {

	AlbumsService albumsService;
	AlbumMessageProducer albumMessageProducer;
	
	@Autowired
	public AlbumsController(AlbumsService albumsService, AlbumMessageProducer albumMessageProducer) {
		super();
		this.albumsService = albumsService;
		this.albumMessageProducer = albumMessageProducer;
	}

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping(value = "/users/{id}/albums", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, })
	public List<AlbumResponseModel> userAlbums(@PathVariable String id) {

		List<AlbumResponseModel> returnValue = new ArrayList<>();

		List<AlbumEntity> albumsEntities = albumsService.getAlbums(id);

		if (albumsEntities == null || albumsEntities.isEmpty()) {
			return returnValue;
		}

		Type listType = new TypeToken<List<AlbumResponseModel>>() {
		}.getType();

		returnValue = new ModelMapper().map(albumsEntities, listType);
		logger.info("Returning " + returnValue.size() + " albums");
		return returnValue;
	}

	@PostMapping(value = "/createAlbum", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> addAlbum(@RequestParam String userID ,@RequestBody CreateAlbumRequestModel createAlbumrequest) {
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		AlbumEntity albumEntity = new AlbumEntity();
		albumEntity.setUserId(userID);
		albumEntity.setId(createAlbumrequest.getId());
		albumEntity.setAlbumId(createAlbumrequest.getAlbumId());
		albumEntity.setName(createAlbumrequest.getName());
		albumEntity.setDescription(createAlbumrequest.getDescription());
		
		AlbumMessage albumMessage = modelMapper.map(albumEntity, AlbumMessage.class);
		AlbumResponseModel returnValue = modelMapper.map(albumMessage, AlbumResponseModel.class);
		if (returnValue != null) {
			albumMessageProducer.sendMessage(albumEntity);
			return new ResponseEntity<>("Album Added", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Album Not Added", HttpStatus.NOT_FOUND);
		}
	}

}
