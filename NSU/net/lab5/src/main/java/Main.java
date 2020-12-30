import proxy.Proxy;

import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;

public class Main {

	public static void main(String[] args) {
		try {
			Proxy proxy = new Proxy(1276);
			proxy.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
