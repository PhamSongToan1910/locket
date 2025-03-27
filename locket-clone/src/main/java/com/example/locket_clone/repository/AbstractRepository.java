package com.example.locket_clone.repository;

import com.example.locket_clone.entities.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractRepository <E extends BaseEntity> implements Repository<E> {
    protected final MongoTemplate mongoTemplate;

    @Override
    public String insert(E entity){
        return mongoTemplate.insert(entity).toString();
    }

    @Override
    public List<E> findAll(Class<E> type){
        return mongoTemplate.findAll(type);
    }

    public List<E> find(Query query, Class<E> type){
        return mongoTemplate.find(query, type);
    }

    @Override
    public E findOne(Query query, Class<E> type){
        return mongoTemplate.findOne(query, type);
    }

    @Override
    public E findById(String id, Class<E> type) {
        Query query = new Query(Criteria.where(BaseEntity._ID).is(new ObjectId(id)));
        return findOne(query, type);
    }

    @Override
    public List<E> findByIds(List<String> ids, Class<E> type) {
        List<ObjectId> listObjectIds = convertListStringToListObject(ids);
        Query query = new Query(Criteria.where(BaseEntity._ID).in(listObjectIds));
        return find(query, type);
    }

    @Override
    public E save(E entity) {
        return mongoTemplate.save(entity);
    }

    @Override
    public List<E> findByIds(Set<String> ids, Class<E> type) {
        Set<ObjectId> setObjectIds = convertSetStringToSetObject(ids);
        Query query = new Query(Criteria.where(BaseEntity._ID).in(ids));
        return find(query, type);
    }

    private List<ObjectId> convertListStringToListObject(List<String> ids){
        return ids.stream().map(ObjectId::new).collect(Collectors.toList());
    }

    private Set<ObjectId> convertSetStringToSetObject(Set<String> ids){
        return ids.stream().map(ObjectId::new).collect(Collectors.toSet());
    }
}
