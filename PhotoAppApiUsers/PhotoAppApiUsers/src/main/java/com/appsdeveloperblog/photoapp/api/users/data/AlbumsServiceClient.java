package com.appsdeveloperblog.photoapp.api.users.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {
        
 //@Retry(name = "albums-ws")
 @RequestMapping("/albumsData/users/{id}/albums")
 @CircuitBreaker(name = "albums-ws" , fallbackMethod="getAlbumsFallBack")
 public List<AlbumResponseModel> getAlbums(@PathVariable("id") String id ,@RequestHeader("Authorization") String authorization);
 
 default List<AlbumResponseModel> getAlbumsFallBack(String id, String authorization, Throwable exception) {
	 System.out.println("Param = "+id);
	 System.out.println("Exception took place:" +exception.getMessage());
	 return new ArrayList<>();
 } 
}