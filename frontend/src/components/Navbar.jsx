import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useRef } from "react";
import { Navigate } from "react-router-dom";

function Navbar() {
  const searchRef = useRef();
  const navigate = useNavigate();

  const onSearchHandler = () => {
    console.log("clicked");
    navigate("/allUsers/" + searchRef.current.value);
  };

  return (
    <div>
      <div class="navbar bg-base-100">
        <div class="flex-1">
          <a class="btn btn-primary bg-tweeter-blue normal-case text-2xl">
            Tweeter
          </a>
        </div>
        <div class="flex-none gap-2">
          <div class="form-control">
            <input
              ref={searchRef}
              type="text"
              placeholder="search an username"
              class="input input-bordered w-full"
              inline
            />
          </div>
        </div>
        <div class="flex-none gap-2 ml-5">
          <button
            className="btn btn-primary bg-tweeter-blue"
            onClick={onSearchHandler}
          >
            Search
          </button>
        </div>
        <div class="flex-none">
          <ul class="menu menu-horizontal px-10">
            <li>
              <a href="/tweets">Tweets</a>
            </li>
            <li>
              <a href="/allUsers/all">All Users</a>
            </li>
            <li>
              <a>Logout</a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default Navbar;
