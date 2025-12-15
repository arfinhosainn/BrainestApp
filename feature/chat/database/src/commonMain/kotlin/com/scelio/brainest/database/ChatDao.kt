package com.scelio.brainest.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scelio.brainest.database.entities.ChatEntity
import com.scelio.brainest.database.entities.MessageEntity

data class ChatWithLastMessageRow(
    @Embedded val chat: ChatEntity,
    @Embedded(prefix = "lastMessage_") val lastMessage: MessageEntity?
)

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChat(chatId: String): ChatEntity?

    @Query("SELECT * FROM chats WHERE userId = :userId ORDER BY lastActivityAt DESC")
    suspend fun getChatsByUserId(userId: String): List<ChatEntity>

    @Query("""
        SELECT 
            c.*,
            m.id AS lastMessage_id,
            m.chatId AS lastMessage_chatId,
            m.content AS lastMessage_content,
            m.role AS lastMessage_role,
            m.createdAt AS lastMessage_createdAt,
            m.senderId AS lastMessage_senderId,
            m.imageUrl AS lastMessage_imageUrl,
            m.fileId AS lastMessage_fileId,
            m.model AS lastMessage_model,
            m.tokensUsed AS lastMessage_tokensUsed,
            m.openAIResponseId AS lastMessage_openAIResponseId
        FROM chats c
        LEFT JOIN messages m ON m.id = (
            SELECT id FROM messages
            WHERE chatId = c.id
            ORDER BY createdAt DESC
            LIMIT 1
        )
        WHERE c.userId = :userId
        ORDER BY c.lastActivityAt DESC
    """)
    suspend fun getChatsWithLastMessage(userId: String): List<ChatWithLastMessageRow>

    @Query("UPDATE chats SET title = :title WHERE id = :chatId")
    suspend fun updateChatTitle(chatId: String, title: String)

    @Query("UPDATE chats SET lastActivityAt = :timestampMs, messageCount = :messageCount WHERE id = :chatId")
    suspend fun updateChatStats(chatId: String, timestampMs: Long, messageCount: Int)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    suspend fun getMessagesByChatId(chatId: String): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId")
    suspend fun getMessageCount(chatId: String): Int

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesByChatId(chatId: String)

    @Query("""
        SELECT 
            c.*,
            m.id AS lastMessage_id,
            m.chatId AS lastMessage_chatId,
            m.content AS lastMessage_content,
            m.role AS lastMessage_role,
            m.createdAt AS lastMessage_createdAt,
            m.senderId AS lastMessage_senderId,
            m.imageUrl AS lastMessage_imageUrl,
            m.fileId AS lastMessage_fileId,
            m.model AS lastMessage_model,
            m.tokensUsed AS lastMessage_tokensUsed,
            m.openAIResponseId AS lastMessage_openAIResponseId
        FROM chats c
        LEFT JOIN messages m ON m.id = (
            SELECT id FROM messages
            WHERE chatId = c.id
            ORDER BY createdAt DESC
            LIMIT 1
        )
        WHERE c.userId = :userId
          AND (
            (c.title IS NOT NULL AND c.title LIKE '%' || :query || '%')
            OR EXISTS (
                SELECT 1 FROM messages mm
                WHERE mm.chatId = c.id
                  AND mm.content LIKE '%' || :query || '%'
            )
          )
        ORDER BY c.lastActivityAt DESC
    """)
    suspend fun searchChatsWithLastMessage(userId: String, query: String): List<ChatWithLastMessageRow>
}