import React from "react";
import { Link } from "react-router-dom";
function Navbar() {
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
              type="text"
              placeholder="search a username"
              class="input input-bordered w-full"
              inline
            />
          </div>
        </div>
        <div class="flex-none">
          <ul class="menu menu-horizontal px-10">
            <li>
              <a href="/tweets">Tweets</a>
            </li>
            <li>
              <a href="/allUsers">All Users</a>
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
