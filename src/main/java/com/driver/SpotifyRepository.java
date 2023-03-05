package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        users.add(user);

        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setLikes(0);

        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist1 = null;

        for(Artist artist:artists){
            if(artist.getName()==artistName){
                artist1=artist;
                break;
            }
        }
        if(artist1==null){
            artist1 = createArtist(artistName);

            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(artist1,l);

            return album;
        }else {
            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> l = artistAlbumMap.get(artist1);
            if(l == null){
                l = new ArrayList<>();
            }
            l.add(album);
            artistAlbumMap.put(artist1,l);

            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Album album = null;
        for(Album album1:albums){
            if(album1.getTitle()==albumName){
                album=album1;
                break;
            }
        }
        if(album==null)
            throw new Exception("Album does not exist");
        else {
            Song song = new Song();
            song.setTitle(title);
            song.setLength(length);
            song.setLikes(0);

            songs.add(song);

            if(albumSongMap.containsKey(album)){
                List<Song> l = albumSongMap.get(album);
                l.add(song);
                albumSongMap.put(album,l);
            }else{
                List<Song> songList = new ArrayList<>();
                songList.add(song);
                albumSongMap.put(album,songList);
            }

            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {


        User user = null;
        for(User u :users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song:songs){
                if(song.getLength()==length){
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);


            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song:songs){
                if(songTitles.contains(song.getTitle())){
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        User user = null;
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User doesn’t exist");

        Playlist playlist = null;
        for(Playlist pl:playlists){
            if(pl.getTitle()==playlistTitle){
                playlist=pl;
                break;
            }
        }
        if(playlist==null)
            throw new Exception("Playlist not exist – create NEW");

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> listener = playlistListenerMap.get(playlist);
        for(User u:listener){
            if(u==user)
                return playlist;
        }

        listener.add(user);
        playlistListenerMap.put(playlist,listener);

        List<Playlist> playl = userPlaylistMap.get(user);
        if(playl == null){
            playl = new ArrayList<>();
        }
        playl.add(playlist);
        userPlaylistMap.put(user,playl);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {


        User user = null;
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");

        Song song = null;
        for(Song s:songs){
            if(s.getTitle()==songTitle){
                song=s;
                break;
            }
        }
        if (song==null)
            throw new Exception("Song not exist");

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }else {
                int like = song.getLikes() + 1;
                song.setLikes(like);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album al_bum:albumSongMap.keySet()){
                    List<Song> songL = albumSongMap.get(al_bum);
                    if(songL.contains(song)){
                        album = al_bum;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist ar :artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(ar);
                    if (albumList.contains(album)){
                        artist = ar;
                        break;
                    }
                }
                int like1 = artist.getLikes() +1;
                artist.setLikes(like1);
                artists.add(artist);
                return song;
            }
        }else {
            int like = song.getLikes() + 1;
            song.setLikes(like);
            List<User> listu = new ArrayList<>();
            listu.add(user);
            songLikeMap.put(song,listu);

            Album album=null;
            for(Album alb:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(alb);
                if(songList.contains(song)){
                    album = alb;
                    break;
                }
            }
            Artist artist = null;
            for(Artist art1:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(art1);
                if (albumList.contains(album)){
                    artist = art1;
                    break;
                }
            }
            int like1 = artist.getLikes() +1;
            artist.setLikes(like1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        //
        int max = 0;
        Artist artist1=null;

        for(Artist artist:artists){
            if(artist.getLikes()>=max){
                artist1=artist;
                max = artist.getLikes();
            }
        }
        if(artist1==null)
            return null;
        else
            return artist1.getName();
    }

    public String mostPopularSong() {
        int max=0;
        Song song = null;

        for(Song so :songLikeMap.keySet()){
            if(so.getLikes()>=max){
                song=so;
                max = so.getLikes();
            }
        }
        if(song==null)
            return null;
        else
            return song.getTitle();
    }
}
