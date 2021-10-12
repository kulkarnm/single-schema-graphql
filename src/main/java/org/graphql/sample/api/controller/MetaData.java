package org.graphql.sample.api.controller;

import java.util.*;

public class MetaData {
    private static final String MESSAGES_KEY = "messages" ;
    private Map<String,Object> attributes = new HashMap<>();

    public MetaData() {}

    public MetaData put(String key,Object value){
        this.attributes.put(key,value);
        return this;
    }
    public MetaData putAll(Map<String,Object> map){
        this.attributes.putAll(map);
        return this;
    }
    public MetaData remove(String key){
        this.attributes.remove(key);
        return this;
    }

    public boolean contains(String key){
        return this.attributes.containsKey(key);
    }

    public MetaData addMessage(Message message){
        Collection<Message> msgCollection = new ArrayDeque<>();
        Object obj = this.attributes.get("messages");
        if(obj !=null && obj instanceof Collection){
            msgCollection = (Collection)obj ;
        }
        ((Collection)msgCollection).add(message);
        return this.put("messages",msgCollection);
    }
    public boolean isEmpty() { return this.attributes.isEmpty();}
    public Map<String,Object> asMap() {return Collections.unmodifiableMap(this.attributes);}

    void merge(MetaData metaData) {
        if (metaData != null && metaData.attributes != null) {
            Iterator<Map.Entry<String, Object>> iterator = metaData.attributes.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                if ("messages".equals(entry.getKey()) && entry.getValue() instanceof Collection) {
                    this.appendMessages((Collection) entry.getValue());
                } else {
                    this.put((String) entry.getKey(), entry.getValue());
                }
            }
        }

    }
    private void appendMessages(Collection<?> messagesCollection){
        Iterator iter = messagesCollection.iterator();
        while(iter.hasNext()){
            Object msgObject = iter.next();
            if(msgObject instanceof Message){
                this.addMessage((Message)msgObject);
            }
        }
    }

    public String toString() { return "Metadata [attributes=" + this.attributes + "]";}

}
