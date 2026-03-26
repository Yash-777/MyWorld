package com.github.yash777.myworld.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.github.yash777.myworld.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>
//, QuerydslPredicateExecutor<User> // QClasses will generate, ClassNotFound: com.querydsl.core.types.Predicate
{

	Optional<User> findUserByEmailId(String username);

}
/*
@Repository
interface UserRepository extends JpaRepository<User, Long>

@NoRepositoryBean
public interface JpaRepository<T, ID> extends ListCrudRepository<T, ID>, ListPagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
	void flush();

	<S extends T> S saveAndFlush(S entity);
	<S extends T> List<S> saveAllAndFlush(Iterable<S> entities);

	@Deprecated
	default void deleteInBatch(Iterable<T> entities) {
		deleteAllInBatch(entities);
	}
	void deleteAllInBatch(Iterable<T> entities);
	void deleteAllByIdInBatch(Iterable<ID> ids);
	void deleteAllInBatch();

	@Deprecated
	T getOne(ID id);
	@Deprecated
	T getById(ID id);

	T getReferenceById(ID id);

	@Override
	<S extends T> List<S> findAll(Example<S> example);

	@Override
	<S extends T> List<S> findAll(Example<S> example, Sort sort);
}

public interface QueryByExampleExecutor<T> {
	<S extends T> Optional<S> findOne(Example<S> example);
	<S extends T> Iterable<S> findAll(Example<S> example);
	<S extends T> Iterable<S> findAll(Example<S> example, Sort sort);
	<S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

	<S extends T> long count(Example<S> example);

	<S extends T> boolean exists(Example<S> example);

	<S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}

@NoRepositoryBean
public interface ListPagingAndSortingRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
	List<T> findAll(Sort sort);
}
@NoRepositoryBean
public interface PagingAndSortingRepository<T, ID> extends Repository<T, ID> {
	Iterable<T> findAll(Sort sort);
	Page<T> findAll(Pageable pageable);
}


@NoRepositoryBean
public interface ListCrudRepository<T, ID> extends CrudRepository<T, ID> {
	<S extends T> List<S> saveAll(Iterable<S> entities);

	List<T> findAll();
	List<T> findAllById(Iterable<ID> ids);
}

package org.springframework.data.repository;
@NoRepositoryBean
public interface CrudRepository<T, ID> extends Repository<T, ID> {
	<S extends T> S save(S entity);
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);

	Optional<T> findById(ID id);
	boolean existsById(ID id);
	Iterable<T> findAll();
	Iterable<T> findAllById(Iterable<ID> ids);

	long count();

	void deleteById(ID id);
	void delete(T entity);
	void deleteAllById(Iterable<? extends ID> ids);
	void deleteAll(Iterable<? extends T> entities);
	void deleteAll();
}

package org.springframework.data.repository;
@Indexed
public interface Repository<T, ID> {
}



public interface JpaSpecificationExecutor<T> {
	Optional<T> findOne(Specification<T> spec);
	List<T> findAll(@Nullable Specification<T> spec);
	Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable);
	List<T> findAll(@Nullable Specification<T> spec, Sort sort);

	long count(@Nullable Specification<T> spec);

	boolean exists(Specification<T> spec);

	long delete(@Nullable Specification<T> spec);

	<S extends T, R> R findBy(Specification<T> spec, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
*/
