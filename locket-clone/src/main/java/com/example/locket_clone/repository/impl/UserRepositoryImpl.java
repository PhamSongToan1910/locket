package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.FindUserBeRequest;
import com.example.locket_clone.entities.response.GetNmberUserOrderByDateResponse;
import com.example.locket_clone.repository.InterfacePackage.UserCustomRepository;
import com.example.locket_clone.utils.DateTimeConvertUtils;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserRepositoryImpl implements UserCustomRepository {

    MongoTemplate mongoTemplate;
//    MongoClient mongoClient;

    @Override
    public List<User> findUserNormal(Pageable pageable, FindUserBeRequest findUserBeRequest) {
        Query query = new Query(Criteria.where(User.AUTHORITIES).ne("admin"));
        query.with(Sort.by(Sort.Direction.DESC, User.CREATE_AT));
        if(Objects.nonNull(findUserBeRequest.getUserId())) {
            query.addCriteria(Criteria.where(User._ID).is(findUserBeRequest.getUserId()));
        }
        if(Objects.nonNull(findUserBeRequest.getUsername())) {
            query.addCriteria(Criteria.where(User.USERNAME).is(findUserBeRequest.getUsername()));
        }
        if(Objects.nonNull(findUserBeRequest.getCreateFrom())) {
            query.addCriteria(Criteria.where(User.CREATE_AT).gte(DateTimeConvertUtils.convertStringToInstant(findUserBeRequest.getCreateFrom())));
        }
        if(Objects.nonNull(findUserBeRequest.getCreateTo())) {
            query.addCriteria(Criteria.where(User.CREATE_AT).lte(DateTimeConvertUtils.convertStringToInstant(findUserBeRequest.getCreateTo())));
        }
        query.with(pageable);
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public List<GetNmberUserOrderByDateResponse> getUserOrderByDay() {
//        MongoDatabase db = mongoClient.getDatabase("locket-clone");
//        MongoCollection<Document> collection = db.getCollection("user");
//
//        Document groupId = new Document("day", new Document("$day", "$" + User.CREATE_AT))
//                .append("$month", "$" + User.CREATE_AT)
//                .append("$year", "$" + User.CREATE_AT);
//        Bson agg = Aggregates.group(groupId, Accumulators.sum("total_user", 1));
//        Bson project = Aggregates.project(
//                new Document("date", new Document("$concat", Arrays.asList("$_id.day", "-$_id.month", "-$_id.year"))).append("total_user", 1)
//        );
//        AggregateIterable<Document> results = collection.aggregate(List.of(agg, project));
//        System.out.println(results.cursor());
//        return null;
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .andExpression("dayOfMonth(" + User.CREATE_AT + ")").as("day")
                        .andExpression("month(" + User.CREATE_AT + ")").as("month")
                        .andExpression("year(" + User.CREATE_AT + ")").as("year"),

                Aggregation.group("day", "month", "year")
                        .count().as("count"),

                Aggregation.project("count")
                        .andExpression("concat(toString(day), '-', toString(month), '-', toString(year))").as("date")
        );
        return mongoTemplate.aggregate(aggregation, "user", GetNmberUserOrderByDateResponse.class).getMappedResults();
    }
}
