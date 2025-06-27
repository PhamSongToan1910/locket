package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.InterfacePackage.UserCustomRepository;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserRepositoryImpl implements UserCustomRepository {

    MongoTemplate mongoTemplate;
//    MongoClient mongoClient;

    @Override
    public List<User> findUserNormal(Pageable pageable) {
        Query query = new Query(Criteria.where(User.AUTHORITIES).ne("admin"));
        query.with(Sort.by(Sort.Direction.DESC, User.CREATE_AT));
        query.with(pageable);
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public List<User> getUserOrderByDay() {
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
                        .andExpression("dayOfMonth(create_at)").as("day")
                        .andExpression("month(create_at)").as("month")
                        .andExpression("year(create_at)").as("year"),

                Aggregation.group("day", "month", "year")
                        .count().as("count"),

                Aggregation.project("count")
                        .andExpression("concat(toString(day), '-', toString(month), '-', toString(year))").as("date")
        );
        return mongoTemplate.aggregate(aggregation, "user", User.class).getMappedResults();
    }
}
