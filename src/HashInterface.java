import java.io.IOException;
import java.io.InputStream;

public interface HashInterface
{
    public void calcHash(InputStream is) throws IOException;

    @Override
    public String toString();
}
