
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Problem {
	private int nbCities;
	private String mediumFileName;
	private String upperFileName;
	private String lowerFileName;
	private int[][] mediumDistances;
	private int[][] upperDistances;
	private int[][] lowerDistances;

	public Problem(int nbCities, String mediumFileName, String upperFileName, String lowerFileName) {
		this.nbCities = nbCities;
		this.mediumFileName = mediumFileName;
		this.lowerFileName = lowerFileName;
		this.upperFileName = upperFileName;

		this.mediumDistances = new int[nbCities][nbCities];
		this.upperDistances = new int[nbCities][nbCities];
		this.lowerDistances = new int[nbCities][nbCities];
	}

	public void constructDistances() {
		for (int i = 0; i < nbCities; i++)
			mediumDistances[i] = new int[nbCities];
		try (BufferedReader br = new BufferedReader(new FileReader(mediumFileName))) {
			String sCurrentLine;
			int i = -1;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] res = sCurrentLine.split("\\s+");
				int h = 0;
				i++;
				for (int j = i + 1; j < nbCities; j++) {
					mediumDistances[i][j] = Integer.parseInt(res[h]);
					mediumDistances[j][i] = mediumDistances[i][j];
					h++;
				}
			}
			for (int k = 0; k < nbCities; k++)
				mediumDistances[k][k] = -10;
		} catch (IOException e) {
			e.printStackTrace();
		}


		for (int i = 0; i < nbCities; i++)
			lowerDistances[i] = new int[nbCities];
		try (BufferedReader br = new BufferedReader(new FileReader(lowerFileName))) {
			String sCurrentLine;
			int i = -1;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] res = sCurrentLine.split("\\s+");
				int h = 0;
				i++;
				for (int j = i + 1; j < nbCities; j++) {
					lowerDistances[i][j] = Integer.parseInt(res[h]);
					lowerDistances[j][i] = lowerDistances[i][j];
					h++;
				}
			}
			for (int k = 0; k < nbCities; k++)
				lowerDistances[k][k] = -10;
		} catch (IOException e) {
			e.printStackTrace();
		}


		for (int i = 0; i < nbCities; i++)
			upperDistances[i] = new int[nbCities];
		try (BufferedReader br = new BufferedReader(new FileReader(upperFileName))) {
			String sCurrentLine;
			int i = -1;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] res = sCurrentLine.split("\\s+");
				int h = 0;
				i++;
				for (int j = i + 1; j < nbCities; j++) {
					upperDistances[i][j] = Integer.parseInt(res[h]);
					upperDistances[j][i] = upperDistances[i][j];
					h++;
				}
			}
			for (int k = 0; k < nbCities; k++)
				upperDistances[k][k] = -10;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public void printDistances() {
	// 	for (int a = 0; a < nbCities; a++) {
	// 		for (int b = 0; b < nbCities; b++) {
	// 			System.out.print(mediumDistances[a][b] + " ");
	// 		}
	// 		System.out.println();
	// 	}
	// }

	public int getNbCities() {
		return nbCities;
	}

	public void setNbCities(int nbCities) {
		this.nbCities = nbCities;
	}

	public String getFileName() {
		return mediumFileName;
	}

	public void setFileName(String fileName) {
		this.mediumFileName = fileName;
	}

	public int[][] getMediumDistances() {
		return mediumDistances;
	}

	public int[][] getUpperDistances() {
		return upperDistances;
	}

	public int[][] getLowerDistances() {
		return lowerDistances;
	}

	public void setMediumDistances(int[][] distances) {
		this.mediumDistances = distances;
	}
	
	public void setLowerDistances(int[][] distances) {
		this.lowerDistances = distances;
	}
	
	public void setUpperDistances(int[][] distances) {
		this.upperDistances = distances;
	}
}
