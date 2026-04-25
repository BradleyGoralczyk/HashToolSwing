import java.io.IOException;
import java.io.InputStream;

public abstract class HashBaseSimple implements HashInterface
{
    abstract protected void processChunk(byte[] chunk, int length);

    @Override
    public void calcHash(InputStream is) throws IOException
    {
        byte[] buffer = new byte[1024];
        for (int amount; (amount = is.read(buffer)) != -1;)
            processChunk(buffer, amount);
    }
}
