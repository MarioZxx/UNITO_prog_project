package src.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * A new ObjectInputStream class that doesn't read the header from the stream when open it
 * */
public class NoReadObjectInputStream extends ObjectInputStream {

  public NoReadObjectInputStream(InputStream inStream) throws IOException {
    super(inStream);
  }

  @Override
  protected void readStreamHeader(){
  }
}
