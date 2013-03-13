describe("spec 1", function() {
	it("is green", function() {
		expect(true).toBe(true);
	});

  it("is baz", function() {
    expect(true).toBe(true);
  });
});

describe("spec 2", function() {
  it("is bar", function() {
    expect(true).toBe(true);
  });

  it("is foo", function() {
    expect(true).toBe(true);
  });

  describe("spec 4", function() {
    it("is blah", function() {
      expect(true).toBe(true);
    });

    it("is blah blah", function() {
      expect(true).toBe(true);
    });
  });
});