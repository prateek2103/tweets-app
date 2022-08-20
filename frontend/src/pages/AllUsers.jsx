import axios from "axios";
import React, { useState } from "react";
import { useEffect } from "react";
import { toast } from "react-toastify";
import { useNavigate, useParams } from "react-router-dom";
function AllUsers() {
  let { username } = useParams();

  const config = {
    headers: {
      Authorization: localStorage.getItem("token"),
    },
  };

  const [users, setUsers] = useState([]);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (username === "all") {
      axios
        .get("http://localhost:8080/users/all", config)
        .then((res) => {
          setUsers(res.data);
          setIsLoading(false);
        })
        .catch((err) => {
          console.log(err);
          navigate("/login");
          toast.error("Please try again later");
        });
    } else {
      axios
        .get("http://localhost:8080/user/search/" + username, config)
        .then((res) => {
          setUsers(res.data);
          setIsLoading(false);
        })
        .catch((err) => {
          console.log(err);
          navigate("/login");
          toast.error("Please try again later");
        });
    }
  }, [username]);

  return (
    <div className="container">
      {!isLoading && (
        <div className="mx-12 mt-10">
          <h1 className="font-thin text-5xl mb-10">Users</h1>
          <div class="overflow-x-auto">
            <table class="table w-full ">
              <thead>
                <tr>
                  <th></th>
                  <th>Firstname</th>
                  <th>LastName</th>
                  <th>Handle</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user, idx) => {
                  return (
                    <tr>
                      <td>{idx + 1}</td>
                      <td>{user.firstName}</td>
                      <td>{user.lastName}</td>
                      <td>@{user.username}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {isLoading && (
        <div>
          <button className="btn btn-primary bg-tweeter-blue">
            Processing...
          </button>
        </div>
      )}

      {!isLoading && users.length == 0 && (
        <div>
          <h1 className="font-thin text-5xl">No Users found!!</h1>
        </div>
      )}
    </div>
  );
}

export default AllUsers;
