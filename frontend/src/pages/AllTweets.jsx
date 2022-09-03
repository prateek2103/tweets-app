import axios from "axios";
import { config } from "daisyui";
import React from "react";
import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { getAllTweets, replyTweet, likeTweet } from "../context/tweetsAction";

function AllTweets() {
  const [showReplies, setShowReplies] = useState(true);
  const [reply, setReply] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [tweets, setTweets] = useState([]);

  useEffect(() => {
    //get all tweets
    getAllTweets()
      .then((res) => {
        setTweets(res.data);
      })
      .catch((err) => {
        toast.error("Server error.Please try again later");
      });

    setIsLoading(false);
  }, []);

  //tweets reply handler
  const tweetReplyHandler = (id) => {
    const replyBox = document.getElementById(id + "replyBox");
    const replyButton = document.getElementById(id + "reply");

    //toggle between reply and submit by user actions
    if (reply === true) {
      replyBox.classList.remove("hidden");
      replyBox.classList.add("block");
      replyButton.innerText = "Submit";
      setReply(false);
    } else {
      let replyMessage = replyBox.children[0].value;

      //http request
      replyTweet(id, replyMessage)
        .then((res) => {
          replyBox.classList.add("hidden");
          replyBox.classList.remove("block");
          replyButton.innerText = "reply";
          setReply(true);
          toast.success("replied to the tweet sucessfully");
        })
        .catch((err) => {
          toast.error("Please try again later");
        });
    }
  };

  //like handler
  const onLikeHandler = (id) => {
    let likes = document.getElementById(id + "likeValue");

    //http request
    likeTweet(id)
      .then((res) => {
        toast.success("tweet liked successfully");
      })
      .catch((err) => {
        toast.error("server error. please try again later");
      });
  };

  //show replies toggler
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

  //method to retrieve date of the tweet
  const getDateOfTweet = (tweetDate) => {
    let dateDiff = new Date() - new Date(tweetDate);
    let seconds = Math.floor(dateDiff / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);
    let days = Math.floor(hours / 24);

    if (days >= 1) {
      return days == 1 ? days + " day ago" : days + " days ago";
    } else if (hours >= 1) {
      return hours == 1 ? hours + " hour ago" : hours + " hours ago";
    } else if (minutes >= 1) {
      return minutes == 1 ? minutes + " minute ago" : minutes + " minutes ago";
    } else {
      return seconds == 1 ? seconds + " second ago" : seconds + " seconds ago";
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
                          {getDateOfTweet(tweet.createdAt)}
                        </span>
                        <h1 className="font-bold inline mr-5">
                          @{tweet.handle}
                        </h1>
                        <span id={tweet.id + "likeValue"}>
                          <button
                            class="material-icons mr-2 text-sm"
                            onClick={() => onLikeHandler(tweet.id)}
                            id={tweet.id + "like"}
                          >
                            thumb_up
                          </button>
                          {tweet.likesOnTweet}
                        </span>
                        <textarea
                          class="textarea w-full resize-none mt-2 block"
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
                                        {getDateOfTweet(reply.createdAt)}
                                      </span>
                                      <h1 className="inline mr-5">
                                        @{reply.handle}
                                      </h1>
                                      <span id={reply.id + "likeValue"}>
                                        <button
                                          class="material-icons mr-2 text-sm"
                                          onClick={() =>
                                            onLikeHandler(reply.id)
                                          }
                                          id={reply.id + "like"}
                                        >
                                          thumb_up
                                        </button>
                                        {reply.likesOnTweet}
                                      </span>
                                      <textarea
                                        class="textarea w-full resize-none mt-2 block"
                                        value={reply.message}
                                        disabled
                                      ></textarea>
                                    </div>
                                  </div>
                                );
                              })}
                            {tweet.replies == null && (
                              <div>
                                <h1 className="text-thin text-center text-1xl">
                                  No replies yet. Be the first one
                                </h1>
                              </div>
                            )}
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
