package com.github.yash777.myworld.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

//@Entity // every '@Entity' class must declare or inherit at least one '@Id' or '@EmbeddedId' property)
@Setter @Getter // No need to apply @Getter and @Setter in child class it inherits and creates getters/setters for child properties.
/*
Lombok annotations like @Getter and @Setter applied in the parent class will be inherited by the child class.
The child class will automatically have access to the getter and setter methods for fields from the parent class without needing to define them explicitly.
You can still apply Lombok annotations in the child class to generate getter and setter methods for any additional fields declared in the child class.
*/
@javax.persistence.MappedSuperclass
public class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "CreatedDate")
	@org.hibernate.annotations.CreationTimestamp
	private Timestamp createdDate;
	
	@Column(name = "UpdatedDate")
	@org.hibernate.annotations.UpdateTimestamp
	private Timestamp updatedDate;
	
}
