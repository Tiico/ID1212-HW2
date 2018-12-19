package ID1212.HW2.shared;

import java.io.*;

public class Serializer {
    public static byte[] serializeObject(Object objectToSerialize)throws IOException{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] byteObject;
        ObjectOutput objectOutputStream = new ObjectOutputStream(byteOut);

        objectOutputStream.writeObject(objectToSerialize);
        objectOutputStream.flush();
        byteObject = byteOut.toByteArray();
        objectOutputStream.close();
        byteOut.close();
        return byteObject;
    }

    public static Object deserialize(byte[] byteObj) throws IOException, ClassNotFoundException{
        ByteArrayInputStream inStream = new ByteArrayInputStream(byteObj);
        ObjectInput inputObject = new ObjectInputStream(inStream);

        return inputObject.readObject();
    }
}
