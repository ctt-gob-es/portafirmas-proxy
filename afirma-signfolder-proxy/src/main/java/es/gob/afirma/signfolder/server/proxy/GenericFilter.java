package es.gob.afirma.signfolder.server.proxy;

public class GenericFilter {

	private final String id;

	private final String description;

	public GenericFilter(final String id, final String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}
}
