package com.its4u.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Data
@Entity
@Table(name = "PLACEHOLDERS")
public class PlaceHolders implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private PlaceHolderId placeHolderId;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "environment",insertable = false, updatable = false)
	private Environments _environment;
	
	@Column(name = "VALUE")
	private String value;
		
	@Column(name = "TYPE")
	private String type;
	
	public PlaceHolders() {
		super();
	}
		
	public PlaceHolders(PlaceHolderId placeHolderId, Environments environment, String value,String type) {
		super();
		this.placeHolderId = placeHolderId;
		this._environment = environment;
		this.value = value;
		this.type = type;
	}

	@Override
	public String toString() {
		return "PlaceHolders [placeHolderId=" + placeHolderId + "]";
	}
	

}
