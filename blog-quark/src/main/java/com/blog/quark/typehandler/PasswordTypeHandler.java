package com.blog.quark.typehandler;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.blog.quark.common.Password;


@MappedTypes({Password.class})
public class PasswordTypeHandler extends BaseTypeHandler<Password> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Password parameter, JdbcType jdbcType) throws SQLException {
        final char[] chars = parameter.get();
        ps.setCharacterStream(i, new CharArrayReader(chars), chars.length);
    }

    @Override
    public Password getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Reader reader = rs.getCharacterStream(columnName);
        return Password.of(getCharsFromCharacterStream(reader));
    }

    @Override
    public Password getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Reader reader = rs.getCharacterStream(columnIndex);
        return Password.of(getCharsFromCharacterStream(reader));
    }

    @Override
    public Password getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Reader reader = cs.getCharacterStream(columnIndex);
        return Password.of(getCharsFromCharacterStream(reader));
    }
    
    
    
    private char[] getCharsFromCharacterStream(final Reader reader) {
        // Password在数据库中长度是: varchar(255)
        final int bufSize = 255;
        char[] buffer = new char[bufSize];
        int readLen = 0, available = bufSize, received = 0;
        try {
            while (-1 != (readLen = reader.read(buffer, received, available))) {
                received += readLen;
                available -= readLen;
                if (0 == available) {
                    // buffer扩容
                    buffer = resizeCharBuffer(buffer);
                    available = buffer.length - received;
                }
            }
            
            //返回根据实际接收字符长度进行调整后的buffer
            return adjustmentCharBuffer(buffer, received);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    private static char[] adjustmentCharBuffer(final char[] buffer, final int actualSize) {
        char[] newBuffer = new char[actualSize];
        System.arraycopy(buffer, 0, newBuffer, 0, actualSize);
        return newBuffer;
    }
    
    
    private static char[] resizeCharBuffer(final char[] buffer) {
        final int bufferSize = buffer.length;
        final int newBufferSize = calculateNewCapacity(bufferSize);
        char[] newBuffer = new char[newBufferSize];
        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        return newBuffer;
    }
    
    
    private static int calculateNewCapacity(final int bufferSize) {
        // char buffer threshold, maximum length limit < 2*threshold
        final int threshold =  8 * 1024 * 1024;
        
        if (bufferSize >= threshold) {
            throw new IllegalArgumentException(String.format("buffer size %d exceed threshold %d.", bufferSize, threshold));
        }
        
        int newBufferSize = 256;
        while (newBufferSize < bufferSize) {
            newBufferSize <<= 1;
        }
        
        if (newBufferSize < threshold) {
            return newBufferSize <<= 1;
        } else {
            return threshold;
        }
    }
    
}
