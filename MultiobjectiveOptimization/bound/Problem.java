import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Problem {
	private int nbCities;
	private String fileName;
	private int[][] distances;

	public Problem(int nbCities, String fileName) {
		this.nbCities = nbCities;
		this.fileName = fileName;
		this.distances = new int[nbCities][nbCities];
	}

	public void constructDistances() {
		for (int i = 0; i < nbCities; i++)
			distances[i] = new int[nbCities];

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String sCurrentLine;
			int i = -1;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] res = sCurrentLine.split("\\s+");
				int h = 0;
				i++;
				for (int j = i + 1; j < nbCities; j++) {
					distances[i][j] = Integer.parseInt(res[h]);
					distances[j][i] = distances[i][j];
					h++;
				}
			}
			for (int k = 0; k < nbCities; k++)
				distances[k][k] = -10;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printDistances() {
		for (int a = 0; a < nbCities; a++) {
			for (int b = 0; b < nbCities; b++) {
				System.out.print(distances[a][b] + " ");
			}
			System.out.println();
		}
	}

	public int getNbCities() {
		return nbCities;
	}

	public void setNbCities(int nbCities) {
		this.nbCities = nbCities;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int[][] getDistances() {
		return distances;
	}

	public void setDistances(int[][] distances) {
		this.distances = distances;
	}
}