import axios from "axios";
import React from "react";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

function TweetsByUsername({ username }) {
  const [tweets, setTweets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

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

  const onTweetEditHandler = (id) => {
    const textBox = document.getElementById(id);
    const submitButton = document.getElementById(id + "Edit");
    if (isSubmitting === false) {
      submitButton.innerText = "submit";
      textBox.disabled = false;
      setIsSubmitting(true);
    } else {
      const oldValue = textBox.value;
      setIsSubmitting(false);
      textBox.disabled = true;
      submitButton.innerText = "edit";

      //update the tweet
      axios
        .put(
          "http://localhost:8080/tweets/" +
            localStorage.getItem("username") +
            "/update/" +
            id,
          { message: textBox.value },
          config
        )
        .then((res) => {
          toast.success("tweet updated successfully");
        })
        .catch((err) => {
          toast.error("Please try again later");
          textBox.value = oldValue;
        });
    }
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
                      <div className="w-full px-2 py-2 grid grid-cols-12">
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
                            id={tweet.id}
                            class="textarea w-3/4 resize-none mt-2"
                            disabled="true"
                          >
                            {tweet.message}
                          </textarea>

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
                              <button
                                id={tweet.id + "Edit"}
                                className="btn btn-sm btn-info float-right mt-12 mr-1"
                                onClick={() => onTweetEditHandler(tweet.id)}
                              >
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
