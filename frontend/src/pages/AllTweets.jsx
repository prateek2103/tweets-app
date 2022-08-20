import axios from "axios";
import React from "react";
import { useState, useEffect } from "react";
function AllTweets() {
  const [showReplies, setShowReplies] = useState(true);
  let tweetsList = [];
  const [isLoading, setIsLoading] = useState(true);
  const [tweets, setTweets] = useState([]);

  useEffect(() => {
    const getTweets = async () => {
      const config = {
        headers: { Authorization: localStorage.getItem("token") },
      };

      try {
        const res = await axios.get("http://localhost:8080/tweets/all", config);
        setTweets(res.data);
        console.log(res.data);
        setIsLoading(false);
      } catch (error) {
        console.log(error);
      }
    };

    getTweets();
  }, []);

  const showRepliesHandler = (id) => {
    const repliesBox = document.getElementById(id);
    const repliesButton = document.getElementById(id + "showReplies");

    if (showReplies === true) {
      repliesBox.classList.remove("collapse-close");
      repliesBox.classList.add("collapse-open");
      repliesButton.innerText = "hide replies";
      setShowReplies(false);
    } else {
      repliesBox.classList.add("collapse-close");
      repliesBox.classList.remove("collapse-open");
      repliesButton.innerText = "show replies";
      setShowReplies(true);
    }
  };

  return (
    <div className="container mx-auto">
      <table class="table w-3/4 mt-10 mx-auto border-collapse">
        <tbody className="w-full">
          {!isLoading &&
            tweets.map((tweet) => {
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
                      <div className="col-span-8">
                        <span className="float-right block">
                          {Math.ceil(
                            (new Date() - new Date(tweet.createdAt)) /
                              (1000 * 60 * 60 * 24)
                          ) + " days ago"}
                        </span>
                        <h1 className="font-bold">@{tweet.handle}</h1>
                        <textarea
                          class="textarea w-full resize-none mt-2"
                          value={tweet.message}
                          disabled
                        ></textarea>

                        <div
                          key={tweet.id}
                          tabIndex="0"
                          className="collapse border border-base-300 bg-base-100 rounded-box"
                          id={tweet.id}
                        >
                          <div class="collapse-content">
                            {tweet.replies != null &&
                              tweet.replies.map((reply) => {
                                return (
                                  <div className="w-full px-5 py-2 grid grid-cols-12">
                                    <div className="col-span-2">
                                      <div class="avatar">
                                        <div class="w-24 rounded-full">
                                          <img src={reply.avatarUrl} />
                                        </div>
                                      </div>
                                    </div>
                                    <div className="col-span-8">
                                      <span className="float-right block">
                                        {Math.ceil(
                                          (new Date() -
                                            new Date(reply.createdAt)) /
                                            (1000 * 60 * 60 * 24)
                                        ) + " days ago"}
                                      </span>
                                      <h1>@{reply.handle}</h1>

                                      <textarea
                                        class="textarea w-full resize-none mt-2"
                                        value={reply.message}
                                        disabled
                                      ></textarea>
                                    </div>
                                  </div>
                                );
                              })}
                          </div>
                        </div>
                        <button
                          className="float-right block btn btn-primary bg-tweeter-blue"
                          id={tweet.id + "showReplies"}
                          onClick={() => showRepliesHandler(tweet.id)}
                        >
                          show replies
                        </button>

                        <button className="float-right block btn btn-primary bg-tweeter-blue mr-5">
                          reply
                        </button>
                      </div>
                    </div>
                  </td>
                </tr>
              );
            })}
          {isLoading && <button class="btn btn-primary">Processing...</button>}
          <tr>
            <td>
              <div className="w-full px-5 py-2 grid grid-cols-12">
                <div className="col-span-2">
                  <div class="avatar">
                    <div class="w-24 rounded-full">
                      <img src="https://placeimg.com/192/192/people" />
                    </div>
                  </div>
                </div>
                <div className="col-span-8">
                  <span className="float-right block">3 hrs ago</span>
                  <h1>@UserHandle</h1>

                  <textarea
                    class="textarea w-full resize-none mt-2"
                    value="this is my first tweet"
                    disabled
                  ></textarea>
                  <div
                    tabIndex="0"
                    className={
                      "collapse border border-base-300 bg-base-100 rounded-box " +
                      showReplies
                    }
                  >
                    <div class="collapse-content">
                      <p>Replies</p>
                    </div>
                  </div>
                  <button
                    className="float-right block btn btn-primary bg-tweeter-blue"
                    onClick={() =>
                      showReplies === "collapse-open"
                        ? setShowReplies("collapse-close")
                        : setShowReplies("collapse-open")
                    }
                  >
                    show replies
                  </button>
                  <button className="float-right block btn btn-primary bg-tweeter-blue mr-5">
                    reply
                  </button>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default AllTweets;
