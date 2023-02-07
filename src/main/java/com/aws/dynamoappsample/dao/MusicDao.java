package com.aws.dynamoappsample.dao;

import com.aws.dynamoappsample.client.DynamoDbConnection;
import com.aws.dynamoappsample.entity.Music;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class MusicDao {
	
 public Music getItem(String pk, String sk) { // To-Do conditional check pk and sk both needed ??? Handle Exceptions ?

        Music result = null;

        try {
            DynamoDbTable<Music> table = DynamoDbConnection.getDynamoDbEnhancedClient().table("Music", TableSchema.fromBean(Music.class));
            Key key = Key.builder()
                .partitionValue(pk).sortValue(sk)
                .build();

            // Get the item by using the key.
            result = table.getItem(
                    (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
 
 public void persistMusic(Music music){
	 
	 System.out.println("Music:::"+music);
	 System.out.println("DynamoDbConnection.getDynamoDbEnhancedClient():"+DynamoDbConnection.getDynamoDbEnhancedClient());

     DynamoDbTable<Music> mappedTable = DynamoDbConnection.getDynamoDbEnhancedClient().
             table("music", TableSchema.fromBean(Music.class));


     mappedTable.putItem(music);
 }
}