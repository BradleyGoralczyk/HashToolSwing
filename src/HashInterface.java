import java.io.IOException;
import java.io.InputStream;

public interface HashInterface
{
    public String calcHash(InputStream is) throws IOException;
}
