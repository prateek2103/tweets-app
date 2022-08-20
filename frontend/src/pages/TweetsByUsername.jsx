import axios from "axios";
import React from "react";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

function TweetsByUsername({ username }) {
  const [tweets, setTweets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const config = {
    headers: {
      Authorization: localStorage.getItem("token"),
    },
  };

  useEffect(() => {
    axios
      .get("http://localhost:8080/tweets/" + username, config)
      .then((res) => {
        setTweets(res.data);
      })
      .then((res) => {
        console.log(tweets);
        setIsLoading(false);
      })
      .catch((error) => {
        console.log(error);
        setIsLoading(false);
      });
  }, []);

  const onTweetDeleteHandler = (id) => {
    axios
      .delete(
        "http://localhost:8080/tweets/" +
          localStorage.getItem("username") +
          "/delete/" +
          id,
        config
      )
      .then((res) => {
        let filteredTweets = tweets.filter((tweet) => tweet.id != id);
        setTweets(filteredTweets);
        toast.success("toast deleted successfully");
      })
      .catch((err) => {
        toast.error("Please try again later");
      });
  };

  return (
    <div className="mt-5">
      {!isLoading && tweets.length > 0 && (
        <div class="overflow-y-scroll w-full h-[300px]">
          <table class="table w-full">
            <tbody>
              {tweets.map((tweet) => {
                return (
                  <tr>
                    <td>
                      <div className="w-full px-5 py-2 grid grid-cols-12">
                        <div className="col-span-2">
                          <div class="avatar">
                            <div class="w-24 rounded-full">
                              <img src={tweet.avatarUrl} />
                            </div>
                          </div>
                        </div>
                        <div className="col-span-10">
                          <span className="float-right block">
                            {Math.ceil(
                              (new Date() - new Date(tweet.createdAt)) /
                                (1000 * 60 * 60 * 24)
                            ) + " days ago"}
                          </span>
                          <h1>@{tweet.handle}</h1>

                          <textarea
                            class="textarea w-3/4 resize-none mt-2"
                            value={tweet.message}
                            disabled
                          ></textarea>

                          {tweet.handle === localStorage.getItem("username") ? (
                            <>
                              <button
                                className="btn btn-sm btn-error float-right mt-12"
                                onClick={() => {
                                  onTweetDeleteHandler(tweet.id);
                                }}
                              >
                                delete
                              </button>
                              <button className="btn btn-sm btn-info float-right mt-12 mr-3">
                                edit
                              </button>
                            </>
                          ) : (
                            <></>
                          )}
                        </div>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {!isLoading && tweets.length == 0 && (
        <h1 className="font-thin text-3xl text-center mt-10 ">No tweets yet</h1>
      )}

      {isLoading && (
        <span className="mt-5 btn btn-primary bg-tweeter-blue">Loading...</span>
      )}
    </div>
  );
}

export default TweetsByUsername;
