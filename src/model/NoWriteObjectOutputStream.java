package src.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A new ObjectOutputStream class that doesn't write the header in the stream when open it
 * */
public class NoWriteObjectOutputStream extends ObjectOutputStream {
  private boolean write;

  public NoWriteObjectOutputStream(OutputStream outStream) throws IOException {
    super(outStream);
  }

  @Override
  protected void writeStreamHeader(){
  }
}
