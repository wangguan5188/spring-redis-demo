
package com.omnipotent.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 将list序列化存储到redis
 */
public class ObjectsTranscoder {

	public static byte[] serialize(List<Object> lists) {  
        if (lists == null) {  
            throw new NullPointerException("Can't serialize null");  
        }  
        byte[] rv=null;  
        ByteArrayOutputStream bos = null;  
        ObjectOutputStream os = null;  
        try {  
            bos = new ByteArrayOutputStream();  
            os = new ObjectOutputStream(bos);  
            for(Object obj : lists){  
                os.writeObject(obj);  
            }  
            os.writeObject(null);  
            os.close();  
            bos.close();  
            rv = bos.toByteArray();  
        } catch (IOException e) {  
          //  throw new IllegalArgumentException("Non-serializable object", e);  
        } finally {
        	if(os!=null){
        		try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
           if(bos!=null){
        	   try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
           }
        }  
        return rv;  
    }  

	/**
	 * 将redis获取的byte[]转化为List
	 * @param in
	 * @return
	 */
    public static List<Object> deserialize(byte[] in) {  
        List<Object> list = new ArrayList<Object>();  
        ByteArrayInputStream bis = null;  
        ObjectInputStream is = null;  
        try {  
            if(in != null) {  
                bis=new ByteArrayInputStream(in);  
                is=new ObjectInputStream(bis);  
                while (true) {  
                    Object obj = (Object) is.readObject();  
                    if(obj == null){  
                        break;  
                    }else{  
                        list.add(obj);  
                    }  
                }  
            }  
        } catch (Exception e) {  
         
        } finally {  
        	if(is!=null){
        		try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(bis!=null){
        		try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }  
        return list;  
    }  
}
