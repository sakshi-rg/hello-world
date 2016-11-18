/**
 * This programs parses a csv containg information of Music Labels, 
 * the albums associated with the Music Label, the tracks in the
 * albums along with track information like artist, track duration,
 * track number. The prorgams contains the algorithm to parse the
 * csv containing simple tokens as well as mixed token.
 * Simple token - contains no comma(',') or quote('"') in a column
 *                value
 * Mixed token - contains comma or quote in a column value
 * Sample csv Data - 
 Header:
 Music Label,Album,Track,Track Number,Artist,Duration (s)
 Info:
 //simple tokens
 label 1,album 1,track 1,2,artist 1,146
 //mixed token with comma in a column
 label 2,"abc xyz, Vol. 7", track 2,1,artist 2,226
 //mixed token with quote and comma in a column
 label 3,album 3,"""hjkk asjdh asjd , akjsd and asjh """,9,artist 3,184
 */

import java.util.*;
import java.io.*;
import java.time.Duration;

/**
 * This class holds the track information
 */
class Track {
    private String title;
    private int trackNumber;
    private String artist;
    private Duration duration;

    public Track(String title, int trackNumber, String artist, long seconds) {
        this.title = title;
        this.trackNumber = trackNumber;
        this.artist = artist;
        this.duration = Duration.ofSeconds(seconds);
    }

    /**
     * Returns the length in seconds of this track
     */
    public long getDuration() {
        return duration.getSeconds();
    }

    /**
     * Returns the artist name of this track
     */
    public String getArtist() {
        return artist;
    }
}

/**
 * This class holds the album information
 */
class Album implements Comparator<Album> {
    private String name;
    private List<Track> tracks;
    
    public Album(String name) {
        this.name = name;
        this.tracks = new ArrayList<Track>();
    }

    /**
     * Returns a set of artists that have tracks in this album
     */
    public Set<String> getArtists() {
        Set<String> artists = new HashSet<String>();
        for(Track t: tracks) {
            artists.add(t.getArtist());
        }
        return artists;
    }

    /**
     * Creates a new track object with the given attributes and adds it to
     * the list of tracks in this album.
     */
    public void addTrack(String title, int trackNumber,
                         String artist, long seconds) {
        Track t = new Track(title, trackNumber, artist, seconds);
        this.tracks.add(t);
    }
    
    /**
     * Returns the total duration in seconds for this album.
     */
    public long getDuration() {
        long total = 0;
        for (Track t: tracks) {
            total += t.getDuration();
        }
        return total;
    }
    
    /**
     * Returns the total duration for this album in hours and minutes
     */
    public String durationToString() {
        String format = "\t\t\t'hours' : %1$d\n\t\t\t'minutes' : %2$d";
        long seconds = this.getDuration();
        int hours = (int) (seconds / 3600);
        int minutes = (int)((seconds - hours*3600) / 60);       
        return String.format(format, hours, minutes);
    }
    
    /**
     * Returns the name of this album
     */
    public String getName() {
        return this.name;
    }

    @Override
    public int compare(Album o1, Album o2) {
        return o1.name.compareTo(o2.name);
    }
}

/**
 * This class holds the MusicLabel information
 */
class MusicLabel implements Comparator<MusicLabel> {
    private String name;
    private Map<String, Album> albums;
    
    public MusicLabel(String name) {
        this.name = name;
        this.albums = new HashMap<String, Album>();
    }
    
    /**
     * Returns the name of this music label
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns an album object corresponding to the given album name
     */
    public Album getAlbum(String albumName) {
        return albums.get(albumName);
    }
    
    /**
     * Returns a list of all albums for this music label
     */
    public List<Album> getAlbums() {
        return new ArrayList<Album>(albums.values());
    }
    
    /**
     * Either adds and returns a new album object with the given album name to
     * the albums map, or returns an existing album object with the given album
     * name
     */
    public Album addAlbum(String albumName) {
        Album a = this.getAlbum(albumName);
        if (a == null) {
            a = new Album(albumName);
            this.albums.put(albumName, a);
        }
        return a;
    }

    /**
     * Returns a list of all artists that have albums with this music label
     */
    public List<String> getArtists() {
        List<String> artists = new ArrayList<String>();
        for(Album a: albums.values()) {
            artists.addAll(a.getArtists());
        }
        return artists;
    }

    @Override
    public int compare(MusicLabel o1, MusicLabel o2) {
        return o1.name.compareTo(o2.name);
    }
}

/**
 * This class represents a structured database to hold the parsed information
 */
class DB {
    Map<String, MusicLabel> labels;

    DB() {
        this.labels = new HashMap<String, MusicLabel>();
    }

    /**
     * Either adds and returns a new MusicLabel object with the given label name
     * to the labels map, or returns an existing label object with the given
     * label name
     */
    MusicLabel addMusicLabel(String labelName) {
        MusicLabel  l = this.labels.get(labelName); 
        if (l == null) {
            l = new MusicLabel(labelName);
            this.labels.put(labelName, l);
        }
        return l;
    }

    /**
     * Inserts a row in the database from the given csvData object
     * @param csvData a vector of strings with the data for a database row
     */
    void add(Vector<String> csvData) {
        // add music label
        String labelName = csvData.get(0);
        MusicLabel l = this.addMusicLabel(labelName);
        // add album
        String albumName = csvData.get(1);
        if (l == null) {
            System.out.println("l is null for " + labelName);
        }
        Album a = l.addAlbum(albumName);
        // add track
        String trackTitle = csvData.get(2);
        int trackNumber = Integer.parseInt(csvData.get(3));
        String artist = csvData.get(4);
        long duration = Long.parseLong(csvData.get(5));
        a.addTrack(trackTitle, trackNumber, artist, duration);
    }
    
    /**
     * Prints the list of all music label names
     */
    public void generateMusicLabels()
    {
        if (this.labels == null || this.labels.size() == 0)
            return;
        System.out.print("[");
        int i = 0;
        for (MusicLabel m: labels.values()) {
            String fmt = "'%1$s%2$s'";
            String endQuote = (i < this.labels.size() - 1) ? ", " : "";
            System.out.print(String.format(fmt, m.getName(), endQuote));
            i++;
        }
        System.out.print("]\n");
    }

    /**
     * Prints the list of all artists for each music label
     */
    public void generateLabelArtists()
    {
        if (this.labels == null)
            return;
        System.out.print("{");
        int i = 0;
        for (MusicLabel m: this.labels.values()) {
            String fmt = "\n\t'%1$s': \n\t\t[";
            System.out.print(String.format(fmt, m.getName()));
            int j = 0;
            List<String> artists = m.getArtists();
            for (String a: artists) {
                fmt = "'%1$s%2$s'";
                String endQuote = (j < artists.size() - 1) ? ", " : "";
                System.out.print(String.format(fmt, a, endQuote));
                j++;
            }
            System.out.print("]");
            if (i < this.labels.size() - 1) {
                System.out.print(", ");
            }
            i++;
        }
        System.out.println("\n}");
    }
      
    /**
     * Prints the duration of all albums associated with all music labels
     */ 
    void generateAlbumDuration()
    {
        if (this.labels == null)
            return;
        System.out.print("{");
        int i = 0;
        for (MusicLabel label: this.labels.values()) {
            String fmt = "\n\t'%1$s': {";
            System.out.println(String.format(fmt, label.getName()));
            int j = 0;
            for (Album a: label.getAlbums()){
                fmt = "\t\t'%1$s': {\n%2$s\n\t\t}%3$s";
                String endQuote = (j < label.getAlbums().size() - 1) ? "," : "";
                System.out.println(String.format(fmt,
                                                 a.getName(),
                                                 a.durationToString(),
                                                 endQuote));
                j++;
            }
            if (i < this.labels.size() - 1) {
                System.out.println("\t},");
            } else {
                System.out.println("\t}");
            }
            i++;
        }
        System.out.println("}");
    }
}

/**
 * This class manages the parsing of a CSV file
 */
class CSVParser {
    Scanner sc;
    Vector<Vector<String>> data;
    int currPos;

    CSVParser(String filename) throws FileNotFoundException {
        this.sc = new Scanner(new FileReader(filename));
        this.data = new Vector<Vector<String>>();
        this.currPos = 0;
    }

    /**
     * Returns true when the iteration reaches the end of the data list
     */
    public boolean hasNext() {
        return currPos < data.size();
    }
    
    /**
     * Returns the next record from the data list
     */
    public Vector<String> getNext() {
        Vector<String> v = data.get(currPos);
        currPos++;
        return v;
    }
    
    /**
	 * Algorithm:
     * Parses a single line from the csv file and inserts a new record in the
     * data list
     */
    private void parseCsvLine(String line) {
        if (line == null || line == "") {
            return;
        }

        Vector<String>  v = new Vector<String>();
        if (line.indexOf('\"') == -1) {
            v.addAll(Arrays.asList(line.split(",")));
            data.add(v);
            return;
        }

        char[] arr = line.toCharArray();
        boolean parsingMixedToken = false;
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (!parsingMixedToken && arr[i] != ',' && arr[i] != '\"') {
                token.append(arr[i]);
            } else if (!parsingMixedToken && arr[i] == ',' && token.length() > 0) {
                //simple token found
                v.add(token.toString());
                token.delete(0, token.length());
            } else if ((!parsingMixedToken) && arr[i] == '\"') {
                //starting of mixed token 
                parsingMixedToken = true;
            } else if (parsingMixedToken) {
                if (arr[i] == '"') {
                  // a", -- end of token
                  if (i-1 > -1 && arr[i-1] != '"' &&
                      ((i+1 < arr.length && arr[i+1] == ',') || i+1 == arr.length)) {
                    parsingMixedToken = false;
                    v.add(token.toString());
                    token.delete(0, token.length());
                  } else  {
                    //else count number of quotes and process
                    int countQuotes = 0;
                    for (int j = i; j < arr.length; j++) {
                      if (arr[j] == '"')
                        countQuotes++;
                      else 
                        break;
                    }
                    for (int j = 0; j < countQuotes/2; j++) {
                      token.append('"');
                    }
                    if (countQuotes%2 == 0) {
                      i = i + countQuotes - 1;
                    } else {
                      //end of token
                      i = i + countQuotes;
                      v.add(token.toString());
                      token.delete(0, token.length());
                      parsingMixedToken = false;
                    }
                  }
                } else {
                    token.append(arr[i]);
                }
            }
        }
        if (token.length() != 0) {
          v.add(token.toString());
        }
        data.add(v);
    }

    /**
     * Reads the csv file line by line and adds the records to the in-memory
     * structure, data.
     */
    public void parse() {
        // Ignore the CSV header
        sc.nextLine();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            parseCsvLine(line);
        }
    }
}


/**
 * main class
 */
public class Parse {

    public static void main(String[] args) throws FileNotFoundException {
        CSVParser csvParser = new CSVParser(argv[1]);
        csvParser.parse();
        DB musicDB = new DB();
        while(csvParser.hasNext()) {
            musicDB.add(csvParser.getNext());
        }
        musicDB.generateMusicLabels();
        musicDB.generateLabelArtists();
        musicDB.generateAlbumDuration();
    }
}
