![Build](https://github.com/andreschaffer/media-streaming-examples/workflows/Build/badge.svg)
[![Test Coverage](https://api.codeclimate.com/v1/badges/d1343c04594ef9155e38/test_coverage)](https://codeclimate.com/github/andreschaffer/media-streaming-examples/test_coverage)
[![Maintainability](https://api.codeclimate.com/v1/badges/d1343c04594ef9155e38/maintainability)](https://codeclimate.com/github/andreschaffer/media-streaming-examples/maintainability)
[![Dependabot](https://img.shields.io/badge/Dependabot-enabled-blue?logo=dependabot)](https://docs.github.com/en/github/administering-a-repository/keeping-your-dependencies-updated-automatically)

# Media Streaming Examples

# Waiting is losing
When serving large media files, it's desirable to allow clients to start playback of the media 
before the entire file has been downloaded, so that they don't have to wait.  
Here we show the use of HTTP [Progressive Download](https://en.wikipedia.org/wiki/Progressive_download) 
to serve videos in that manner.

# [RFC 7233 - Range Requests](https://tools.ietf.org/html/rfc7233)
TL;DR flow explanation:  
- The client makes an initial request for a video and receives a server response with no payload so far,
 but with the headers "Accept-Ranges: bytes" and "Content-Length: _video_length_in_bytes_" indicating that 
 it accepts range requests to deliver partial contents.  
- The client then makes a range request for the first part of the video using the header 
 "Range: bytes=0-" and receives a 206 Partial Content response back from the server with the bytes 
 corresponding to the first part of the video as payload, together with the header "Content-Range: 0-_video_part_byte_end_position_/_video_length_in_bytes_".  
- The client keeps making new requests as the video parts are played, e.g. "Range: bytes=_next_video_part_byte_start_position_-_next_video_part_byte_end_position_".  
- Since we are serving potentially large files, it makes sense to take advantage of HTTP caching mechanisms in order to avoid unnecessary retransmissions over the wire. 
 We achieve that via Conditional Requests ([RFC 7232](https://tools.ietf.org/html/rfc7232), [https://github.com/andreschaffer/http-caching-and-concurrency-examples](https://github.com/andreschaffer/http-caching-and-concurrency-examples));
 in this project specifically we explored that with the headers "Last-Modified" and "If-Range".

# Remember, not all viewers are equal
This project does not explore adapting playback to the viewers' network conditions. So, depending on their bandwith, even with progressive downloads, buffering waiting times may still happen. A common technique to improve on that is known as [Adaptive Bitrate Streaming](https://en.wikipedia.org/wiki/Adaptive_bitrate_streaming), which aims to deliver the optimal viewer experience considering their bandwith.

For a complete read about video streaming, check the excellent source [HowVideo.works](https://howvideo.works/).

# Project Requirements
- Java 14
- Maven

# Trying it out
- Run ./run.sh .  
This will build, test and run the application. You'll be prompted for the videos directory 
where you have the mp4 files you want to serve. 
(Note: there is no pagination in place here, so beware if you are trying to serve a directory with lots of files)  
- Browse to the videos at [http://localhost:8080/videos](http://localhost:8080/videos).  
- Pick a video to watch to!  
You can see in your browser Network Panel or in the video progress bar that parts of the video are
being downloaded as it gets played.

# Contributing
If you would like to help making this project better, see the [CONTRIBUTING.md](CONTRIBUTING.md).  

# Maintainers
Send any other comments, flowers and suggestions to [André Schaffer](https://github.com/andreschaffer).

# License
This project is distributed under the [MIT License](LICENSE).
