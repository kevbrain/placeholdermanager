package com.its4u.repositories;

import org.springframework.data.repository.CrudRepository;

import com.its4u.models.PlaceHolderId;
import com.its4u.models.PlaceHolders;


public interface PlaceHoldersRepository extends CrudRepository<PlaceHolders,PlaceHolderId>{

}
