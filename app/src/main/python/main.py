from pafy import new
from youtubesearchpython import VideosSearch



def get_url_data(prompt) -> dict:

   # videos = VideosSearch(prompt,region="IN")

    try:
        videos = VideosSearch(prompt)

    except Exception as e:
        return {"error":str(e)}

    result = videos.result()["result"]
    videos_list = []

    for results in result:
        title = results["title"]
        url = results["link"]
        publishedTime = results["publishedTime"]
        duration = results["duration"]
        viewCount = results["viewCount"]["short"]
        thumbnail = results["thumbnails"][0]["url"]
        channel = results["channel"]["name"]
        channel_url = results["channel"]["thumbnails"][0]["url"]

        videos_list.append({"title":title,"url":url,"publishedTime":publishedTime,"duration":duration,"viewCount":viewCount,"thumbnail":thumbnail,"channel":channel,"channel_url":channel_url})

    return videos_list


def get_stream_url(url:str):
    try:
        video = new(url=url)
        stream_url = video.getbestaudio().url
        return stream_url

    except Exception as e:
        return str(e)



# print(get_stream_url("https://www.youtube.com/watch?v=ryD8BqVexJI"))
print(get_url_data("new york nagaram"))