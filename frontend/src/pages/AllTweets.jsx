import axios from "axios";
import { config } from "daisyui";
import React from "react";
import { useState, useEffect } from "react";
import { toast } from "react-toastify";

function AllTweets() {
  const [showReplies, setShowReplies] = useState(true);
  const [reply, setReply] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [tweets, setTweets] = useState([]);

  const config = {
    headers: { Authorization: localStorage.getItem("token") },
  };

  useEffect(() => {
    const getTweets = async () => {
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

  const tweetReplyHandler = (id) => {
    const replyBox = document.getElementById(id + "replyBox");
    const replyButton = document.getElementById(id + "reply");

    if (reply === true) {
      replyBox.classList.remove("hidden");
      replyBox.classList.add("block");
      replyButton.innerText = "Submit";
      setReply(false);
    } else {
      // post request
      axios
        .post(
          "http://localhost:8080/tweets/" +
            localStorage.getItem("username") +
            "/reply/" +
            id,
          {
            message: replyBox.children[0].value,
            handle: localStorage.getItem("username"),
            createdAt: new Date(),
          },
          config
        )
        .then((res) => {
          replyBox.classList.add("hidden");
          replyBox.classList.remove("block");
          replyButton.innerText = "reply";
          setReply(true);
          toast.success("replied to a tweet sucessfully");
        })
        .catch((err) => {
          toast.error("Please try again later");
        });
    }
  };

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
                        <div id={tweet.id + "replyBox"} className="hidden">
                          <textarea
                            class="textarea w-full resize-none border-2 border-gray-300 "
                            placeholder="What's do you think?"
                            maxLength="144"
                          ></textarea>
                        </div>
                        <button
                          className="float-right block btn btn-primary bg-tweeter-blue"
                          id={tweet.id + "showReplies"}
                          onClick={() => showRepliesHandler(tweet.id)}
                        >
                          show replies
                        </button>

                        <button
                          className="float-right block btn btn-primary bg-tweeter-blue mr-5"
                          id={tweet.id + "reply"}
                          onClick={() => tweetReplyHandler(tweet.id)}
                        >
                          reply
                        </button>
                      </div>
                    </div>
                  </td>
                </tr>
              );
            })}
          {isLoading && <button class="btn btn-primary">Processing...</button>}
        </tbody>
      </table>
    </div>
  );
}

export default AllTweets;
