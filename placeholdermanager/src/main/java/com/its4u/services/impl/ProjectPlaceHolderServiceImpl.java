package com.its4u.services.impl;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.its4u.models.PlaceHolders;
import com.its4u.repositories.PlaceHoldersRepository;
import com.its4u.services.PlaceHolderService;

@Service
public class ProjectPlaceHolderServiceImpl implements PlaceHolderService{
	
	@Autowired
	private PlaceHoldersRepository repository;

	@Override
	public List<PlaceHolders> getAllPlaceHolders() {
	
		return IterableUtils.toList(repository.findAll());
	}

}
