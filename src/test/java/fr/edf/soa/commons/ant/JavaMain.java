package fr.edf.soa.commons.ant;

public class JavaMain {
	public static void main(String[] args) {
		String info = null;

		if (args.length == 0)
			throw new IllegalArgumentException("no args");
		if (args.length == 1) {
			info = args[0];
		} else
			throw new IllegalArgumentException("bad args, only one expected.");

		try {
			if (info.equalsIgnoreCase("success"))
				System.out.println("logging at info level [" + info + "]");
			else if (info.equalsIgnoreCase("failure"))
				throw new Exception("failure");
			System.exit(0);
		} catch (Exception e) {
			System.err
					.println("logging at error level, failed with an exception."
							+ e.toString());
			System.exit(3);
		}
	}
}