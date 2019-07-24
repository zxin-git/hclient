package com.zxin.java.common.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public enum JacksonMapper{
	
	JSON(new ObjectMapper()), 
	XML(new XmlMapper()), 
//	YMAL(new YamlMapper()),
	;
	
	private final ObjectMapper mapper;

	private JacksonMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	public <T> String toString(T t) throws JsonProcessingException{
		return this.mapper.writeValueAsString(t);
	} ;
	
	public <T> T toObject(String content, TypeReference<T> typeReference) 
			throws JsonParseException, JsonMappingException, IOException{
		return this.mapper.readValue(content, typeReference);
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
};