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
    User usr = new User();
    usr.setName(name);
    usr.setMobile(mobile);
    return usr;
    }

    public Artist createArtist(String name) {
        Artist art = new Artist();
        art.setName(name);
        art.setLikes(0);

        artists.add(art);
        return art;
    }

    public Album createAlbum(String title, String artistName) {
    Artist artisst = null;
    for (Artist a :artists){
        if(a.getName() == artistName){
            artisst = a;
            break;
        }
    }
    if (artists == null){
        artisst = createArtist(artistName);
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(new Date());

        albums.add(album);
        List<Album> a = new ArrayList<>();
        a.add(album);
        artistAlbumMap.put(artisst,a);

        return album;
    }else {
        // already we have artist
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(new Date());

        albums.add(album);
        List<Album> a = artistAlbumMap.get(artisst);
        if (a == null) {
            a = new ArrayList<>();
        }
        a.add(album);
        artistAlbumMap.put(artisst, a);

        return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
    Album album = null;
    for (Album al : albums){
        if(al.getTitle() == albumName){
            album = al;
            break;
        }
    }
    if (album == null){
        throw new Exception("Album not present- make NEW");
    }else{
        Song song = new Song();     // since no song present create new
        song.setLength(length);
        song.setTitle(title);
        song.setLikes(0);
        // add in the list
        songs.add(song);

        if(albumSongMap.containsKey(album)){
            List<Song> s = albumSongMap.get(album);
            s.add(song);
            albumSongMap.put(album,s);
        }else{
            List<Song> s_list = new ArrayList<>();
            s_list.add(song);
            albumSongMap.put(album, s_list);
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
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> sl = new ArrayList<>();
            for(Song song:songs){
                if(song.getLength()==length){
                    sl.add(song);
                }
            }
            playlistSongMap.put(playlist,sl);

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
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> sname = new ArrayList<>();
            for(Song song:songs){
                if(songTitles.contains(song.getTitle())){
                    sname.add(song);
                }
            }
            playlistSongMap.put(playlist,sname);

            List<User> ulist = new ArrayList<>();
            ulist.add(user);
            playlistListenerMap.put(playlist,ulist);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> uPlayList = userPlaylistMap.get(user);
                uPlayList.add(playlist);
                userPlaylistMap.put(user,uPlayList);
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
            throw new Exception("Song does not exist");

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
