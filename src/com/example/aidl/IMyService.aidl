  
package com.example.aidl;  
import com.example.aidl.ICallback;  
  
interface IMyService {  
      
    void init(String packageName,String slot);  
    void registerCallback(String packageName,ICallback cb);  
    void unregisterCallback(String packageName,ICallback cb);  
      
} 