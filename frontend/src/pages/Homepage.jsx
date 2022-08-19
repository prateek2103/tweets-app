import React, { useRef, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import MyTweets from "./MyTweets";
import axios from "axios";
function Homepage() {
  const [tweet, setTweet] = useState("");

  const onChangeHandler = (e) => {
    setTweet(e.target.value);
  };

  const onSubmitHandler = () => {
    if (tweet.length > 144) {
      toast.error("tweet length cannot be more than 144 characters");
    } else if (tweet.length == 0) {
      toast.error("tweet cannot be empty");
    } else {
      //create a pull request
      let reqBody = {
        handle: localStorage.getItem("username"),
        createdAt: new Date(),
        message: tweet,
      };

      const config = {
        headers: {
          Authorization: localStorage.getItem("token"),
        },
      };

      axios
        .post(
          "http://localhost:8080/tweets/" +
            localStorage.getItem("username") +
            "/add",
          reqBody,
          config
        )
        .then((res) => {
          toast.success("tweet posted successfully");
          setTweet("");
        });
    }
  };

  return (
    <div>
      <div className="grid grid-cols-12 min-w-screen">
        <div className="col-span-4 mt-[15%] px-5 text-center">
          <div class="avatar online placeholder">
            <div class="bg-ghost-focus text-black-content border-2 border-black rounded-full w-[250px]">
              <span class="text-2xl">P</span>
            </div>
          </div>
          <div className="text-center mt-[15%]">
            <Link to="/forget-password" className="block text-tweeter-blue">
              Forget Password?
            </Link>
            <span className="text-md">Don't worry. We got you</span>
          </div>
        </div>
        <div className="col-span-8 px-[100px] ">
          <div className="mt-[10%]">
            <textarea
              onChange={onChangeHandler}
              class="textarea w-full resize-none border-2 border-gray-300"
              value={tweet}
              placeholder="What's on your mind"
              maxLength="144"
            ></textarea>

            <button
              className="btn btn-primary float-right bg-tweeter-blue"
              onClick={onSubmitHandler}
            >
              Tweet
            </button>
          </div>

          <div className="mt-[100px]">
            <h1 className="font-thin text-5xl">My tweets</h1>
            <MyTweets></MyTweets>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Homepage;
