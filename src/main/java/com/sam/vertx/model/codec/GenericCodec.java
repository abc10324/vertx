package com.sam.vertx.model.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class GenericCodec<T> implements MessageCodec<T, T> {

	private final Class<T> targetClass;
	
	public GenericCodec(Class<T> targetClass) {
		super();
		this.targetClass = targetClass;
	}
	
	@Override
	public void encodeToWire(Buffer buffer, T s) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(s);
            out.flush();
            byte[] yourBytes = bos.toByteArray();
            buffer.appendInt(yourBytes.length);
            buffer.appendBytes(yourBytes);
            out.close();
        } catch (IOException e) {
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {}
        }  
	}

	@Override
	public T decodeFromWire(int pos, Buffer buffer) {
		// My custom message starting from this *position* of buffer
        int _pos = pos;

        // Length of JSON
        int length = buffer.getInt(_pos);

        // Jump 4 because getInt() == 4 bytes
        byte[] yourBytes = buffer.getBytes(_pos += 4, _pos += length);
        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            @SuppressWarnings("unchecked")
            T msg = (T) ois.readObject();
            ois.close();
            return msg;
        } catch (IOException | ClassNotFoundException e) {
        	System.out.println("Listen failed "+e.getMessage());
        } finally {
            try {
                bis.close();
            } catch (IOException e) {}
        }
        return null;
	}

	@Override
	public T transform(T customMessage) {
		return customMessage;
	}

	@Override
	public String name() {
		return targetClass.getSimpleName() + "Codec";
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
	
}
