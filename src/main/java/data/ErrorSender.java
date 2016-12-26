/**
 * 
 */
package data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ErrorSender
{
	public static boolean sendError(String info)
	{
		//Uebertraegt die Fehlermeldung an den Server per http-POST
		URL url;
		try
		{
			StringBuffer response = new StringBuffer();
			String smessage = "errormessage=" + URLEncoder.encode(info.toString(), "UTF-8");  //$NON-NLS-1$//$NON-NLS-2$

			url = new URL("http://www.vennmaker.com/errorcenter.php"); //$NON-NLS-1$
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setUseCaches(false);
			http.setRequestMethod("POST"); //$NON-NLS-1$

			http.connect();
			DataOutputStream out = new DataOutputStream(http.getOutputStream());

			out.writeBytes(smessage);

			out.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(http
					.getInputStream()));

			String input = ""; //$NON-NLS-1$
			while ((input = in.readLine()) != null)
			{
				response.append(input + "\r"); //$NON-NLS-1$
			}
			

			out.close();

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
